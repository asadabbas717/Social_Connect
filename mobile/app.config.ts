import type { ExpoConfig } from 'expo/config';
import { existsSync } from 'node:fs';
import { resolve } from 'node:path';

const androidConfig = process.env.FIREBASE_ANDROID_CONFIG ?? '../app/google-services.json';
const iosConfig = process.env.FIREBASE_IOS_CONFIG ?? './firebase/GoogleService-Info.plist';
const config: ExpoConfig = {
  name: 'Loopnest',
  slug: 'loopnest',
  version: '1.0.0',
  scheme: 'loopnest',
  icon: './assets/loopnest-icon.png',
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
  ],
};
export default config;
