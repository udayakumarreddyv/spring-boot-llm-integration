#!/bin/bash
cd "$(dirname "$0")"
echo "Starting HTTP server on http://localhost:8081/"
echo "Access the frontend at: http://localhost:8081/"
python3 -m http.server 8081
