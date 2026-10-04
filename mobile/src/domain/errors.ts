export function friendlyError(error: unknown): string {
  const code = error && typeof error === 'object' && 'code' in error ? String(error.code) : '';
  if (/permission-denied|unauthorized/.test(code)) return 'You do not have permission to do that.';
  if (/network|unavailable/.test(code))
    return 'Could not connect. Check your connection and try again.';
  if (/too-many-requests|resource-exhausted/.test(code))
    return 'Too many requests. Please try again later.';
  if (/invalid-credential|wrong-password|user-not-found/.test(code))
    return 'Email or password is incorrect.';
  if (/email-already-in-use/.test(code)) return 'This email is already registered. Try signing in.';
  if (/weak-password/.test(code)) return 'Please choose a stronger password.';
  if (/auth\//.test(code))
    return 'Could not complete authentication. Check your details and try again.';
  if (error instanceof Error && !code) return error.message;
  return 'Something went wrong. Please try again.';
}
