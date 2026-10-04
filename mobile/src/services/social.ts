import {
  collection,
  doc,
  getDoc,
  getDocs,
  query,
  orderBy,
  limit,
  startAfter,
  onSnapshot,
  runTransaction,
  serverTimestamp,
  setDoc,
  deleteField,
  FieldPath,
} from '@react-native-firebase/firestore';
import { auth, db } from './firebase';
import {
  decodeProfile,
  decodePost,
  decodeComment,
  profileFields,
  postText,
  commentText,
  type Post,
  type Comment,
} from '../domain/social';

const requireUser = () => {
  if (!auth.currentUser) throw new Error('Please sign in again.');
  return auth.currentUser.uid;
};
export const profileRef = (uid: string) => doc(db, 'users', uid);
export async function readProfile(uid: string) {
  const snapshot = await getDoc(profileRef(uid));
  return decodeProfile(snapshot.data() ?? {});
}
export function watchProfile(
  uid: string,
  next: (value: ReturnType<typeof decodeProfile>) => void,
  error: (e: unknown) => void,
) {
  return onSnapshot(
    profileRef(uid),
    (snapshot) => next(decodeProfile(snapshot.data() ?? {})),
    error,
  );
}
export async function saveProfile(name: string, bio: string) {
  const uid = requireUser();
  const fields = profileFields(name, bio);
  await runTransaction(db, async (transaction) => {
    const target = profileRef(uid);
    const existing = await transaction.get(target);
    transaction.set(
      target,
      {
        ...fields,
        ...(!existing.exists() || !existing.get('createdAt')
          ? { createdAt: serverTimestamp() }
          : {}),
      },
      { merge: true },
    );
  });
}
export function watchFeed(next: (posts: Post[]) => void, error: (e: unknown) => void) {
  return onSnapshot(
    query(collection(db, 'posts'), orderBy('timestamp', 'desc'), limit(30)),
    (snapshot) => {
      next(
        snapshot.docs
          .map((item) => decodePost(item.id, item.data()))
          .filter((item): item is Post => item !== null),
      );
    },
    error,
  );
}
export async function olderPosts(lastId: string) {
  const cursor = await getDoc(doc(db, 'posts', lastId));
  if (!cursor.exists()) return [];
  const snapshot = await getDocs(
    query(collection(db, 'posts'), orderBy('timestamp', 'desc'), startAfter(cursor), limit(30)),
  );
  return snapshot.docs
    .map((item) => decodePost(item.id, item.data()))
    .filter((item): item is Post => item !== null);
}
export async function createPost(text: string) {
  const uid = requireUser();
  const content = postText(text);
  await setDoc(doc(collection(db, 'posts')), {
    uid,
    text: content,
    imageUrl: '',
    timestamp: serverTimestamp(),
    likes: {},
  });
}
export async function toggleLike(id: string) {
  const uid = requireUser();
  return runTransaction(db, async (transaction) => {
    const target = doc(db, 'posts', id);
    const current = await transaction.get(target);
    if (!current.exists()) throw new Error('This post is no longer available.');
    const likes = current.get('likes') as Record<string, boolean> | undefined;
    transaction.update(
      target,
      new FieldPath('likes', uid),
      likes?.[uid] === true ? deleteField() : true,
    );
    const updated = { ...likes };
    if (updated[uid] === true) delete updated[uid];
    else updated[uid] = true;
    return updated;
  });
}
export function watchComments(
  postId: string,
  next: (comments: Comment[]) => void,
  error: (e: unknown) => void,
) {
  return onSnapshot(
    query(collection(db, 'posts', postId, 'comments'), orderBy('timestamp', 'desc'), limit(100)),
    (snapshot) => {
      next(
        snapshot.docs
          .map((item) => decodeComment(item.id, item.data()))
          .filter((item): item is Comment => item !== null)
          .reverse(),
      );
    },
    error,
  );
}
export async function addComment(postId: string, text: string) {
  const uid = requireUser();
  const content = commentText(text);
  const parent = doc(db, 'posts', postId);
  const target = doc(collection(parent, 'comments'));
  await runTransaction(db, async (transaction) => {
    if (!(await transaction.get(parent)).exists())
      throw new Error('This post is no longer available.');
    transaction.set(target, { id: target.id, uid, text: content, timestamp: serverTimestamp() });
  });
}
