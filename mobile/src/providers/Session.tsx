import { createContext, useContext, useEffect, useState, type PropsWithChildren } from 'react';
import { onAuthStateChanged, type User } from '@react-native-firebase/auth';
import { auth } from '../services/firebase';

const SessionContext = createContext<{ user: User | null; loading: boolean }>({
  user: null,
  loading: true,
});
export function SessionProvider({ children }: PropsWithChildren) {
  const [state, setState] = useState<{ user: User | null; loading: boolean }>({
    user: null,
    loading: true,
  });
  useEffect(() => onAuthStateChanged(auth, (user) => setState({ user, loading: false })), []);
  return <SessionContext.Provider value={state}>{children}</SessionContext.Provider>;
}
export const useSession = () => useContext(SessionContext);
