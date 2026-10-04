import { useState } from 'react';
import { Text, View } from 'react-native';
import {
  createUserWithEmailAndPassword,
  sendPasswordResetEmail,
  signInWithEmailAndPassword,
} from '@react-native-firebase/auth';
import { auth } from '../services/firebase';
import { authInput } from '../domain/social';
import { useAction } from '../hooks/useAction';
import { Button, ErrorText, Field, Heading, Page, styles } from '../components/ui';

export default function SignIn() {
  const [mode, setMode] = useState<'login' | 'register' | 'reset'>('login');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [notice, setNotice] = useState('');
  const { run, busy, error, setError } = useAction();
  function switchMode(next: typeof mode) {
    setMode(next);
    setError('');
    setNotice('');
    setPassword('');
  }
  const submit = () =>
    run(async () => {
      const input = authInput(email, mode === 'reset' ? 'reset' : password, mode === 'register');
      if (mode === 'reset') {
        await sendPasswordResetEmail(auth, input.email);
        setNotice('If this email has an account, you’ll receive password reset instructions.');
      } else if (mode === 'register')
        await createUserWithEmailAndPassword(auth, input.email, input.password);
      else await signInWithEmailAndPassword(auth, input.email, input.password);
    });
  return (
    <Page>
      <View style={{ marginTop: 28 }}>
        <Text style={styles.small}>A PLACE FOR YOUR PEOPLE</Text>
        <View style={{ height: 18 }} />
        <Heading
          title={
            mode === 'register'
              ? 'Join the conversation.'
              : mode === 'reset'
                ? 'Find your way back.'
                : 'Good to see you.'
          }
          subtitle={
            mode === 'register'
              ? 'Create an account and share your first moment.'
              : mode === 'reset'
                ? 'We’ll help you reset your password.'
                : 'Share moments. Meet people. Stay connected.'
          }
        />
        <Field
          label="Email"
          value={email}
          onChangeText={setEmail}
          autoCapitalize="none"
          keyboardType="email-address"
          autoComplete="email"
          editable={!busy}
        />
        {mode !== 'reset' && (
          <Field
            label="Password"
            value={password}
            onChangeText={setPassword}
            secureTextEntry
            autoCapitalize="none"
            autoComplete={mode === 'register' ? 'new-password' : 'current-password'}
            editable={!busy}
          />
        )}
        <ErrorText message={error} />
        {notice && (
          <Text accessibilityLiveRegion="polite" style={[styles.copy, { marginBottom: 16 }]}>
            {notice}
          </Text>
        )}
        <Button
          title={
            mode === 'register'
              ? 'Create account'
              : mode === 'reset'
                ? 'Send reset instructions'
                : 'Sign in'
          }
          busy={busy}
          onPress={submit}
        />
        <Button
          secondary
          title={mode === 'login' ? 'Create an account' : 'Back to sign in'}
          disabled={busy}
          onPress={() => switchMode(mode === 'login' ? 'register' : 'login')}
        />
        {mode === 'login' && (
          <Button
            secondary
            title="Forgot password?"
            disabled={busy}
            onPress={() => switchMode('reset')}
          />
        )}
      </View>
    </Page>
  );
}
