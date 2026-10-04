import { useState } from 'react';
import { Image, Text } from 'react-native';
import { router } from 'expo-router';
import { createPost } from '../services/social';
import { chooseImage } from '../services/images';
import { limits, type PickedImage } from '../domain/social';
import { useAction } from '../hooks/useAction';
import { Button, ErrorText, Field, Heading, Page, styles } from '../components/ui';
export default function CreatePost() {
  const [text, setText] = useState('');
  const [image, setImage] = useState<PickedImage>();
  const { run, busy, error } = useAction();
  return (
    <Page>
      <Heading
        title="What’s on your mind?"
        subtitle="A thought, a photo, a moment worth sharing."
      />
      <Field
        label="Your post"
        multiline
        value={text}
        onChangeText={setText}
        maxLength={limits.post}
        editable={!busy}
        placeholder="Start a conversation…"
      />
      <Text style={[styles.small, { marginBottom: 18 }]}>
        {text.length}/{limits.post}
      </Text>
      {image && (
        <>
          <Image
            source={{ uri: image.uri }}
            style={[styles.image, { marginBottom: 16 }]}
            accessibilityLabel="Selected post image"
          />
          <Button
            secondary
            title="Remove image"
            disabled={busy}
            onPress={() => setImage(undefined)}
          />
        </>
      )}
      <ErrorText message={error} />
      <Button
        secondary
        title="Choose a photo"
        disabled={busy}
        onPress={() =>
          run(async () => {
            const selected = await chooseImage();
            if (selected) setImage(selected);
          })
        }
      />
      <Button
        secondary
        title="Take a photo"
        disabled={busy}
        onPress={() =>
          run(async () => {
            const selected = await chooseImage(true);
            if (selected) setImage(selected);
          })
        }
      />
      <Button
        title="Publish post"
        busy={busy}
        onPress={() =>
          run(async () => {
            await createPost(text, image);
            router.back();
          })
        }
      />
    </Page>
  );
}
