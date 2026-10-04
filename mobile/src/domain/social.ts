export type Profile = { name: string; bio: string; imageUrl: string; createdAt: number | null };
export type Post = {
  id: string;
  uid: string;
  text: string;
  imageUrl: string;
  timestamp: number | null;
  likes: Record<string, boolean>;
};
export type Comment = { id: string; uid: string; text: string; timestamp: number | null };
export type PickedImage = { uri: string; mimeType: string; size: number };

export const limits = {
  post: 5000,
  comment: 2000,
  name: 80,
  bio: 500,
  imageBytes: 5 * 1024 * 1024,
};
const string = (value: unknown) => (typeof value === 'string' ? value : '');
const date = (value: unknown): number | null => {
  if (
    value &&
    typeof value === 'object' &&
    'toMillis' in value &&
    typeof value.toMillis === 'function'
  ) {
    const result: unknown = value.toMillis();
    return typeof result === 'number' && Number.isFinite(result) ? result : null;
  }
  return null;
};
export function decodeProfile(data: Record<string, unknown>): Profile {
  return {
    name: string(data.name),
    bio: string(data.bio),
    imageUrl: string(data.imageUrl),
    createdAt: date(data.createdAt),
  };
}
export function decodePost(id: string, data: Record<string, unknown>): Post | null {
  if (!string(data.uid)) return null;
  const likes: Record<string, boolean> = {};
  if (data.likes && typeof data.likes === 'object') {
    for (const [key, value] of Object.entries(data.likes)) if (value === true) likes[key] = true;
  }
  return {
    id,
    uid: string(data.uid),
    text: string(data.text),
    imageUrl: string(data.imageUrl),
    timestamp: date(data.timestamp),
    likes,
  };
}
export function decodeComment(id: string, data: Record<string, unknown>): Comment | null {
  if (!string(data.uid) || !string(data.text)) return null;
  return { id, uid: string(data.uid), text: string(data.text), timestamp: date(data.timestamp) };
}
export function profileFields(name: string, bio: string, imageUrl?: string) {
  name = name.trim();
  bio = bio.trim();
  if (!name || name.length > limits.name)
    throw new Error(`Name must contain 1–${limits.name} characters.`);
  if (bio.length > limits.bio) throw new Error(`Bio must be at most ${limits.bio} characters.`);
  return { name, bio, ...(imageUrl === undefined ? {} : { imageUrl }) };
}
export function postText(text: string, hasImage: boolean) {
  const result = text.trim();
  if (!result && !hasImage) throw new Error('Write something or choose an image.');
  if (result.length > limits.post)
    throw new Error(`Posts can contain up to ${limits.post} characters.`);
  return result;
}
export function commentText(text: string) {
  const result = text.trim();
  if (!result || result.length > limits.comment)
    throw new Error(`Comments must contain 1–${limits.comment} characters.`);
  return result;
}
export function validateImage(image: PickedImage) {
  if (!['image/jpeg', 'image/png', 'image/webp'].includes(image.mimeType))
    throw new Error('Choose a JPEG, PNG or WebP image.');
  if (image.size <= 0 || image.size > limits.imageBytes)
    throw new Error('Images must be smaller than 5 MB.');
}
export function authInput(email: string, password: string, register = false) {
  const trimmed = email.trim();
  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(trimmed)) throw new Error('Enter a valid email address.');
  if (!password || (register && password.length < 6))
    throw new Error(
      register ? 'Use a password with at least 6 characters.' : 'Enter your password.',
    );
  return { email: trimmed, password };
}
