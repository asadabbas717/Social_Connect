import { Redirect } from 'expo-router';
import { useSession } from '../providers/Session';
export default function Index() {
  const { user } = useSession();
  return <Redirect href={user ? '/(tabs)' : '/sign-in'} />;
}
