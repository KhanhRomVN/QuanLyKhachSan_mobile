#!/bin/bash

# Define colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${GREEN}🚀 Starting Auto-Deploy Process...${NC}"

# Path to the APK
APK_PATH="app/build/outputs/apk/debug/app-debug.apk"
PACKAGE_NAME="com.example.hotel_management"
MAIN_ACTIVITY=".ui.auth.LoginActivity"

# 1. Build the APK
echo -e "\n${GREEN}📦 Building APK...${NC}"
./gradlew clean assembleDebug

if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Build Failed! Fix errors and try again.${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Build Successful!${NC}"

# 2. Check for connected device
echo -e "\n${GREEN}🔍 Checking for connected device...${NC}"
DEVICE=$(adb devices | grep -w "device" | grep -v "List of devices" | head -n 1 | awk '{print $1}')

if [ -z "$DEVICE" ]; then
    echo -e "${RED}❌ No device connected via ADB. Please check Genymotion or connect a device.${NC}"
    exit 1
fi

echo -e "📲 Found device: ${DEVICE}"

# 3. Install the APK
echo -e "\n${GREEN}⬇️ Installing APK to ${DEVICE}...${NC}"
adb -s "$DEVICE" install -r "$APK_PATH"

if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Installation Failed!${NC}"
    exit 1
fi

echo -e "${GREEN}✅ App installed successfully!${NC}"

# 4. Launch the App
echo -e "\n${GREEN}🚀 Launching App...${NC}"
adb -s "$DEVICE" shell am start -n "${PACKAGE_NAME}/${MAIN_ACTIVITY}"

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ App Launched! Happy Coding! 🎉${NC}"
else
    echo -e "${RED}⚠️ Failed to launch app automatically.${NC}"
fi
