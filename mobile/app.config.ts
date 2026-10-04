import type { ExpoConfig } from 'expo/config';
import { existsSync } from 'node:fs';
import { resolve } from 'node:path';

const androidConfig = process.env.FIREBASE_ANDROID_CONFIG ?? '../app/google-services.json';
const iosConfig = process.env.FIREBASE_IOS_CONFIG ?? './firebase/GoogleService-Info.plist';
const config: ExpoConfig = {
  name: 'Social Connect',
  slug: 'social-connect',
  version: '1.0.0',
  scheme: 'socialconnect',
  icon: './assets/icon.png',
  orientation: 'portrait',
  userInterfaceStyle: 'light',
  platforms: ['android', 'ios'],
  ios: {
    bundleIdentifier: 'com.example.socialconnect',
    supportsTablet: true,
    ...(existsSync(resolve(__dirname, iosConfig)) ? { googleServicesFile: iosConfig } : {}),
  },
  android: { package: 'com.example.socialconnect', googleServicesFile: androidConfig },
  plugins: [
    'expo-router',
    'expo-dev-client',
    '@react-native-firebase/app',
    '@react-native-firebase/auth',
    ['expo-build-properties', { ios: { useFrameworks: 'dynamic' } }],
    [
      'expo-image-picker',
      {
        photosPermission: 'Choose a photo to share or use on your profile.',
        cameraPermission: 'Take a photo to share with your community.',
        microphonePermission: false,
      },
    ],
  ],
};
export default config;
