#!/bin/sh

echo "NEXT_PUBLIC_API_URL=$NEXT_PUBLIC_API_URL" > /app/.env

exec yarn dev