import { useCallback, useState } from 'react';
import { Text, View } from 'react-native';
import { router, useFocusEffect } from 'expo-router';
import { useSession } from '../../providers/Session';
import { watchProfile } from '../../services/social';
import { friendlyError } from '../../domain/errors';
import type { Profile } from '../../domain/social';
import { Avatar, Button, ErrorText, Heading, Loading, Page, styles } from '../../components/ui';

export default function ProfileScreen() {
  const { user } = useSession();
  const [profile, setProfile] = useState<Profile | null>(null);
  const [error, setError] = useState('');
  useFocusEffect(
    useCallback(() => {
      if (!user) return;
      return watchProfile(
        user.uid,
        (value) => {
          setProfile(value);
          setError('');
        },
        (e) => setError(friendlyError(e)),
      );
    }, [user]),
  );
  return (
    <Page>
      <ErrorText message={error} />
      {!profile && !error ? (
        <Loading />
      ) : (
        <>
          <View style={{ alignItems: 'center', marginVertical: 24 }}>
            <Avatar name={profile?.name || 'You'} url={profile?.imageUrl} size={96} />
          </View>
          <Heading
            title={profile?.name || 'Make yourself at home.'}
            subtitle={profile?.bio || 'Add your name, a photo and a little about yourself.'}
          />
          {profile?.createdAt && (
            <Text style={[styles.small, { marginBottom: 24 }]}>
              Joined {new Date(profile.createdAt).toLocaleDateString()}
            </Text>
          )}
          <Button
            title={profile?.name ? 'Edit profile' : 'Set up your profile'}
            onPress={() => router.push('/edit-profile')}
          />
        </>
      )}
    </Page>
  );
}
