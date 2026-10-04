import { useCallback, useState } from 'react';
import { FlatList, Text, View } from 'react-native';
import { useFocusEffect, useLocalSearchParams } from 'expo-router';
import { watchComments, addComment } from '../../services/social';
import { limits, type Comment } from '../../domain/social';
import { friendlyError } from '../../domain/errors';
import { useAction } from '../../hooks/useAction';
import { Author } from '../../components/Author';
import { Button, ErrorText, Field, Loading, Page, styles } from '../../components/ui';
export default function Comments() {
  const params = useLocalSearchParams<{ id: string }>();
  const id = Array.isArray(params.id) ? params.id[0] : params.id;
  const [comments, setComments] = useState<Comment[]>([]);
  const [text, setText] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const { run, busy, error: actionError } = useAction();
  useFocusEffect(
    useCallback(() => {
      if (!id || id.includes('/')) {
        setError('This post is not available.');
        setLoading(false);
        return;
      }
      return watchComments(
        id,
        (value) => {
          setComments(value);
          setLoading(false);
          setError('');
        },
        (e) => {
          setError(friendlyError(e));
          setLoading(false);
        },
      );
    }, [id]),
  );
  return (
    <Page scroll={false}>
      <View style={{ flex: 1, paddingHorizontal: 20 }}>
        <ErrorText message={error} />
        {loading ? (
          <Loading />
        ) : (
          <FlatList
            data={comments}
            keyExtractor={(item) => item.id}
            contentContainerStyle={{ paddingVertical: 12 }}
            renderItem={({ item }) => (
              <View style={styles.card}>
                <Author uid={item.uid} timestamp={item.timestamp} />
                <Text selectable style={styles.copy}>
                  {item.text}
                </Text>
              </View>
            )}
            ListEmptyComponent={
              <Text style={styles.subtitle}>No comments yet. Start the conversation.</Text>
            }
          />
        )}
        <View style={{ paddingTop: 14 }}>
          <Field
            label="Your comment"
            value={text}
            onChangeText={setText}
            maxLength={limits.comment}
            multiline
            editable={!busy}
          />
          <ErrorText message={actionError} />
          <Button
            title="Send comment"
            busy={busy}
            disabled={!id || !!error}
            onPress={() =>
              run(async () => {
                await addComment(id, text);
                setText('');
              })
            }
          />
        </View>
      </View>
    </Page>
  );
}
