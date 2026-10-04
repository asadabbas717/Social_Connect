import { useEffect, useState } from 'react';
import { View } from 'react-native';
import { router } from 'expo-router';
import { useSession } from '../providers/Session';
import { readProfile, saveProfile } from '../services/social';
import { chooseImage } from '../services/images';
import { limits, type PickedImage } from '../domain/social';
import { friendlyError } from '../domain/errors';
import { useAction } from '../hooks/useAction';
import { Avatar, Button, ErrorText, Field, Loading, Page } from '../components/ui';
export default function EditProfile() {
  const { user } = useSession();
  const [name, setName] = useState('');
  const [bio, setBio] = useState('');
  const [url, setUrl] = useState('');
  const [image, setImage] = useState<PickedImage>();
  const [loading, setLoading] = useState(true);
  const [loadError, setLoadError] = useState('');
  const [attempt, setAttempt] = useState(0);
  const { run, busy, error } = useAction();
  useEffect(() => {
    if (!user) return;
    let active = true;
    readProfile(user.uid)
      .then((profile) => {
        if (active) {
          setName(profile.name);
          setBio(profile.bio);
          setUrl(profile.imageUrl);
        }
      })
      .catch((e) => {
        if (active) setLoadError(friendlyError(e));
      })
      .finally(() => {
        if (active) setLoading(false);
      });
    return () => {
      active = false;
    };
  }, [user, attempt]);
  if (loading)
    return (
      <Page>
        <Loading />
      </Page>
    );
  if (loadError)
    return (
      <Page>
        <ErrorText message={loadError} />
        <Button
          title="Try again"
          onPress={() => {
            setLoading(true);
            setLoadError('');
            setAttempt((value) => value + 1);
          }}
        />
      </Page>
    );
  return (
    <Page>
      <View style={{ alignItems: 'center', marginVertical: 20 }}>
        <Avatar name={name || 'You'} url={image?.uri || url} size={100} />
      </View>
      <Button
        secondary
        title="Choose profile photo"
        disabled={busy}
        onPress={() =>
          run(async () => {
            const selected = await chooseImage();
            if (selected) setImage(selected);
          })
        }
      />
      <Field
        label="Name"
        value={name}
        onChangeText={setName}
        maxLength={limits.name}
        editable={!busy}
      />
      <Field
        label="Bio"
        value={bio}
        onChangeText={setBio}
        maxLength={limits.bio}
        multiline
        editable={!busy}
      />
      <ErrorText message={error} />
      <Button
        title="Save profile"
        busy={busy}
        onPress={() =>
          run(async () => {
            await saveProfile(name, bio, image);
            router.back();
          })
        }
      />
    </Page>
  );
}
