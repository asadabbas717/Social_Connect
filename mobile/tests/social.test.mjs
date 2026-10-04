import { test } from 'node:test';
import assert from 'node:assert/strict';
import {
  authInput,
  profileFields,
  postText,
  commentText,
  validateImage,
  decodePost,
  decodeComment,
  decodeProfile,
  limits,
} from '../src/domain/social.ts';
import { friendlyError } from '../src/domain/errors.ts';

test('text-only profile edit preserves stored avatar, join date, and unrelated fields', () => {
  const before = {
    name: 'Before',
    bio: '',
    imageUrl: 'old-avatar',
    createdAt: 100,
    fcmToken: 'token',
  };
  const after = { ...before, ...profileFields(' Ada ', ' Engineer ') };
  assert.deepEqual(after, {
    name: 'Ada',
    bio: 'Engineer',
    imageUrl: 'old-avatar',
    createdAt: 100,
    fcmToken: 'token',
  });
});
test('new avatar changes image without modifying creation metadata', () => {
  const fields = profileFields('Ada', '', 'new-avatar');
  assert.equal(fields.imageUrl, 'new-avatar');
  assert.equal('createdAt' in fields, false);
});
test('profile rejects empty names and oversized bios', () => {
  assert.throws(() => profileFields('  ', ''));
  assert.throws(() => profileFields('Ada', 'a'.repeat(limits.bio + 1)));
});
test('image-only post is allowed but empty and oversized posts are rejected', () => {
  assert.equal(postText(' ', true), '');
  assert.throws(() => postText(' ', false));
  assert.throws(() => postText('x'.repeat(limits.post + 1), false));
});
test('comments validate trim, empty and length boundaries', () => {
  assert.equal(commentText(' Hi '), 'Hi');
  assert.throws(() => commentText(' \n '));
  assert.throws(() => commentText('x'.repeat(limits.comment + 1)));
});
test('password characters are preserved and email normalized', () => {
  assert.deepEqual(authInput(' a@example.test ', ' password '), {
    email: 'a@example.test',
    password: ' password ',
  });
  assert.throws(() => authInput('not-email', 'password'));
  assert.throws(() => authInput('a@example.test', 'short', true));
});
test('image validation rejects oversized, unknown-size, and non-image uploads', () => {
  assert.doesNotThrow(() =>
    validateImage({ uri: 'file://test', mimeType: 'image/png', size: 100 }),
  );
  for (const [mimeType, size] of [
    ['image/png', limits.imageBytes + 1],
    ['image/png', 0],
    ['text/html', 100],
  ]) {
    assert.throws(() => validateImage({ uri: 'file://test', mimeType, size }));
  }
});
test('legacy posts lacking likes and images remain readable', () => {
  assert.deepEqual(decodePost('id', { uid: 'user', text: 'Legacy' }), {
    id: 'id',
    uid: 'user',
    text: 'Legacy',
    imageUrl: '',
    timestamp: null,
    likes: {},
  });
  assert.equal(decodePost('id', { text: 'No owner' }), null);
});
test('pending timestamps and malformed likes do not crash feed decoding', () => {
  assert.equal(decodePost('id', { uid: 'user', timestamp: null })?.timestamp, null);
  assert.deepEqual(
    decodePost('id', { uid: 'user', likes: { user: true, other: false, malformed: 'yes' } })?.likes,
    { user: true },
  );
});
test('comment identity comes from the document and malformed comments are skipped', () => {
  assert.equal(
    decodeComment('actual-id', { id: 'wrong-id', uid: 'u', text: 'hello' })?.id,
    'actual-id',
  );
  assert.equal(decodeComment('id', { uid: 'u' }), null);
});
test('profile decodes absent fields and Firebase timestamps', () => {
  assert.deepEqual(decodeProfile({}), { name: '', bio: '', imageUrl: '', createdAt: null });
  assert.equal(decodeProfile({ createdAt: { toMillis: () => 123 } }).createdAt, 123);
});
test('Firebase errors do not expose provider details to the user', () => {
  const error = Object.assign(new Error('sensitive provider detail'), {
    code: 'firestore/permission-denied',
  });
  assert.equal(friendlyError(error), 'You do not have permission to do that.');
});
