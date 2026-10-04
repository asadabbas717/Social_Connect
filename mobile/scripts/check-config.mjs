import { readFileSync, existsSync } from 'node:fs';
import { resolve } from 'node:path';

const platform = process.argv[2] ?? 'android';
if (!['android', 'ios'].includes(platform)) throw new Error('Use android or ios.');
const file = resolve(
  platform === 'android'
    ? (process.env.FIREBASE_ANDROID_CONFIG ?? '../app/google-services.json')
    : (process.env.FIREBASE_IOS_CONFIG ?? './firebase/GoogleService-Info.plist'),
);
if (!existsSync(file))
  throw new Error(`Missing ${platform} Firebase client configuration. See firebase/README.md.`);
if (platform === 'android') {
  const value = JSON.parse(readFileSync(file, 'utf8'));
  if (
    !value.client?.some(
      (client) =>
        client.client_info?.android_client_info?.package_name === 'com.example.socialconnect',
    )
  )
    throw new Error('Android Firebase configuration must contain com.example.socialconnect.');
  if ('private_key' in value)
    throw new Error('Use client configuration, never a service-account key.');
} else {
  const value = readFileSync(file, 'utf8');
  if (
    !value.includes('<key>BUNDLE_ID</key>') ||
    !value.includes('<string>com.example.socialconnect</string>')
  )
    throw new Error('iOS Firebase configuration must register com.example.socialconnect.');
}
console.log(
  `${platform} Firebase client configuration found and application ID checked. No backend requests made.`,
);
