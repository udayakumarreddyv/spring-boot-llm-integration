angular.module('llmApp', [])
.controller('ChatController', ['$http', '$timeout', '$sce', function($http, $timeout, $sce) {
    var vm = this;
    
    // Configuration
    vm.apiBaseUrl = 'http://localhost:8080/api/v1/llm';
    
    // Application state
    vm.messages = [];
    vm.userMessage = '';
    vm.isLoading = false;
    vm.error = null;
    vm.selectedProvider = '';
    vm.availableProviders = [];
    vm.providerStatuses = {};
    vm.healthStatus = {
        status: 'UNKNOWN',
        availableProviders: 0,
        providers: []
    };
    
    // Settings
    vm.settings = {
        model: '',
        maxTokens: 500,
        temperature: 0.7
    };
    
    // Initialize the application
    vm.init = function() {
        vm.checkHealth();
        vm.loadProviders();
    };
    
    // Check application health
    vm.checkHealth = function() {
        $http.get(vm.apiBaseUrl + '/health')
            .then(function(response) {
                vm.healthStatus = response.data;
                vm.clearError();
            })
            .catch(function(error) {
                vm.handleError('Failed to check service health', error);
                vm.healthStatus = {
                    status: 'DOWN',
                    availableProviders: 0,
                    providers: []
                };
            });
    };
    
    // Load available providers
    vm.loadProviders = function() {
        $http.get(vm.apiBaseUrl + '/providers')
            .then(function(response) {
                vm.availableProviders = response.data;
                vm.checkProviderStatuses();
                vm.clearError();
            })
            .catch(function(error) {
                vm.handleError('Failed to load providers', error);
                vm.availableProviders = [];
            });
    };
    
    // Check status of all providers
    vm.checkProviderStatuses = function() {
        vm.availableProviders.forEach(function(provider) {
            $http.get(vm.apiBaseUrl + '/providers/' + provider + '/status')
                .then(function(response) {
                    vm.providerStatuses[provider] = response.data.available;
                })
                .catch(function(error) {
                    vm.providerStatuses[provider] = false;
                });
        });
    };
    
    // Handle provider change
    vm.onProviderChange = function() {
        vm.clearError();
    };
    
    // Send message to LLM
    vm.sendMessage = function() {
        if (!vm.userMessage.trim() || vm.isLoading) {
            return;
        }
        
        // Add user message to chat
        vm.addMessage('user', vm.userMessage.trim());
        
        // Prepare the request
        var request = {
            messages: vm.getMessagesForAPI(),
            provider: vm.selectedProvider || undefined,
            model: vm.settings.model || undefined,
            max_tokens: vm.settings.maxTokens,
            temperature: vm.settings.temperature
        };
        
        // Clear input and set loading state
        var userMessage = vm.userMessage;
        vm.userMessage = '';
        vm.isLoading = true;
        vm.clearError();
        
        // Send request to API
        var url = vm.apiBaseUrl + '/chat/completions';
        if (vm.selectedProvider) {
            url += '?provider=' + encodeURIComponent(vm.selectedProvider);
        }
        
        $http.post(url, request)
            .then(function(response) {
                vm.handleChatResponse(response.data);
            })
            .catch(function(error) {
                vm.handleError('Failed to get response from LLM', error);
                // Remove the user message if there was an error
                if (vm.messages.length > 0 && vm.messages[vm.messages.length - 1].content === userMessage) {
                    vm.messages.pop();
                }
                vm.userMessage = userMessage; // Restore the message
            })
            .finally(function() {
                vm.isLoading = false;
                vm.scrollToBottom();
            });
    };
    
    // Handle chat response
    vm.handleChatResponse = function(response) {
        if (response.choices && response.choices.length > 0) {
            var choice = response.choices[0];
            var content = choice.message.content;
            
            // Add assistant message with metadata
            var message = vm.addMessage('assistant', content);
            message.metadata = {
                provider: response.provider,
                model: response.model,
                usage: response.usage,
                finishReason: choice.finish_reason
            };
        } else {
            vm.handleError('Received empty response from LLM', null);
        }
    };
    
    // Add message to chat
    vm.addMessage = function(role, content) {
        var message = {
            role: role,
            content: content,
            timestamp: new Date()
        };
        vm.messages.push(message);
        return message;
    };
    
    // Get messages formatted for API
    vm.getMessagesForAPI = function() {
        return vm.messages.map(function(msg) {
            return {
                role: msg.role,
                content: msg.content
            };
        });
    };
    
    // Handle keyboard events
    vm.handleKeyDown = function(event) {
        if (event.ctrlKey && event.keyCode === 13) { // Ctrl+Enter
            event.preventDefault();
            vm.sendMessage();
        }
    };
    
    // Clear chat
    vm.clearChat = function() {
        if (confirm('Are you sure you want to clear the chat?')) {
            vm.messages = [];
            vm.clearError();
        }
    };
    
    // Add system message
    vm.addSystemMessage = function() {
        var systemMessage = prompt('Enter system message:');
        if (systemMessage) {
            vm.addMessage('system', systemMessage.trim());
        }
    };
    
    // Export chat
    vm.exportChat = function() {
        var chatData = {
            timestamp: new Date().toISOString(),
            provider: vm.selectedProvider || 'default',
            settings: vm.settings,
            messages: vm.messages
        };
        
        var dataStr = JSON.stringify(chatData, null, 2);
        var dataBlob = new Blob([dataStr], {type: 'application/json'});
        
        var link = document.createElement('a');
        link.href = window.URL.createObjectURL(dataBlob);
        link.download = 'llm-chat-' + new Date().toISOString().slice(0, 19).replace(/:/g, '-') + '.json';
        link.click();
    };
    
    // Format message content
    vm.formatMessage = function(content) {
        // Basic formatting: convert newlines to <br> and trust the HTML
        var formatted = content.replace(/\n/g, '<br>');
        return $sce.trustAsHtml(formatted);
    };
    
    // Scroll to bottom of chat
    vm.scrollToBottom = function() {
        $timeout(function() {
            var chatContainer = document.getElementById('chatContainer');
            if (chatContainer) {
                chatContainer.scrollTop = chatContainer.scrollHeight;
            }
        }, 100);
    };
    
    // Error handling
    vm.handleError = function(message, error) {
        console.error(message, error);
        
        var errorMessage = message;
        if (error && error.data) {
            if (error.data.message) {
                errorMessage += ': ' + error.data.message;
            } else if (typeof error.data === 'string') {
                errorMessage += ': ' + error.data;
            }
        } else if (error && error.message) {
            errorMessage += ': ' + error.message;
        }
        
        vm.error = errorMessage;
    };
    
    // Clear error
    vm.clearError = function() {
        vm.error = null;
    };
    
    // Initialize the application when controller loads
    vm.init();
}]);

// Configure HTTP provider for CORS
angular.module('llmApp').config(['$httpProvider', function($httpProvider) {
    $httpProvider.defaults.useXDomain = true;
    delete $httpProvider.defaults.headers.common['X-Requested-With'];
}]);
