import { Alert, Text, View } from 'react-native';
import { signOut } from '@react-native-firebase/auth';
import { auth } from '../../services/firebase';
import { useSession } from '../../providers/Session';
import { useAction } from '../../hooks/useAction';
import { Button, ErrorText, Heading, Page, styles } from '../../components/ui';
export default function Settings() {
  const { user } = useSession();
  const { run, busy, error } = useAction();
  return (
    <Page>
      <Heading title="A little housekeeping." subtitle="Your account and preferences." />
      <View style={styles.card}>
        <Text style={styles.small}>SIGNED IN AS</Text>
        <Text style={styles.copy}>{user?.email}</Text>
      </View>
      <ErrorText message={error} />
      <Button
        title="Sign out"
        secondary
        busy={busy}
        onPress={() =>
          Alert.alert('Sign out?', 'You can sign back in with your email and password.', [
            { text: 'Cancel', style: 'cancel' },
            { text: 'Sign out', onPress: () => run(() => signOut(auth)) },
          ])
        }
      />
    </Page>
  );
}
