import { useCallback, useEffect, useRef, useState } from 'react';
import { friendlyError } from '../domain/errors';

/** Ref guard blocks a second tap before React renders the disabled state. */
export function useAction() {
  const locked = useRef(false);
  const mounted = useRef(true);
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState('');
  useEffect(() => {
    mounted.current = true;
    return () => {
      mounted.current = false;
    };
  }, []);
  const run = useCallback(async (action: () => Promise<void>) => {
    if (locked.current) return;
    locked.current = true;
    setBusy(true);
    setError('');
    try {
      await action();
    } catch (e) {
      if (mounted.current) setError(friendlyError(e));
    } finally {
      locked.current = false;
      if (mounted.current) setBusy(false);
    }
  }, []);
  return { run, busy, error, setError };
}
