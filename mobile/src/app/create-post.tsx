import { useState } from 'react';
import { Text } from 'react-native';
import { router } from 'expo-router';
import { createPost } from '../services/social';
import { limits } from '../domain/social';
import { useAction } from '../hooks/useAction';
import { Button, ErrorText, Field, Heading, Page, styles } from '../components/ui';
export default function CreatePost() {
  const [text, setText] = useState('');
  const { run, busy, error } = useAction();
  return (
    <Page>
      <Heading title="What’s on your mind?" subtitle="Share a thought and start a conversation." />
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
      <ErrorText message={error} />
      <Button
        title="Publish post"
        busy={busy}
        onPress={() =>
          run(async () => {
            await createPost(text);
            router.back();
          })
        }
      />
    </Page>
  );
}
