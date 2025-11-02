# Development Stage
# Use a standard Node image for quick installs and easy debugging.
FROM node:18-alpine

# Set the working directory
WORKDIR /app

# Copy package files first to leverage Docker's cache.
# If these don't change, the 'yarn install' step will be skipped.
COPY package.json yarn.lock ./

# Install all dependencies (dev, production, etc.) for development
# Using regular 'yarn install' is fine for dev.
RUN yarn install

# Copy the rest of the application source code
COPY . .

# Expose the development port (e.g., Next.js dev server default is 3000)
# This is often used with hot-reloading.
EXPOSE 3000

# Add startup script
COPY start.sh /app/start.sh
RUN chmod +x /app/start.sh

# Set a command to run your development server.
# For many Node apps (like Next.js), this is 'yarn dev' or 'npm run dev'.
CMD ["/app/start.sh"]