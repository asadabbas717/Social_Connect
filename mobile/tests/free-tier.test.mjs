import { test } from 'node:test';
import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';

test('development dependencies allow only app, email-auth and Firestore Firebase SDKs', () => {
  const manifest = JSON.parse(readFileSync(new URL('../package.json', import.meta.url), 'utf8'));
  const firebase = Object.keys({ ...manifest.dependencies, ...manifest.devDependencies })
    .filter((name) => name.startsWith('@react-native-firebase/'))
    .sort();
  assert.deepEqual(firebase, [
    '@react-native-firebase/app',
    '@react-native-firebase/auth',
    '@react-native-firebase/firestore',
  ]);
});
