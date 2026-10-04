import { getApp } from '@react-native-firebase/app';
import { getAuth } from '@react-native-firebase/auth';
import { getFirestore } from '@react-native-firebase/firestore';
import { getStorage } from '@react-native-firebase/storage';

// Native SDKs load platform client configuration at build time; no privileged credentials.
export const auth = getAuth(getApp());
export const db = getFirestore(getApp());
export const storage = getStorage(getApp());
