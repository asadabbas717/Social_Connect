import { useCallback, useState } from 'react';
import { FlatList, Text, View } from 'react-native';
import { router, useFocusEffect } from 'expo-router';
import { watchFeed, olderPosts } from '../../services/social';
import type { Post } from '../../domain/social';
import { friendlyError } from '../../domain/errors';
import { useAction } from '../../hooks/useAction';
import { Button, ErrorText, Heading, Loading, Page, styles } from '../../components/ui';
import { PostCard } from '../../components/PostCard';

export default function Feed() {
  const [attempt, setAttempt] = useState(0);
  return <FeedContent key={attempt} retry={() => setAttempt((value) => value + 1)} />;
}
function FeedContent({ retry }: { retry: () => void }) {
  const [posts, setPosts] = useState<Post[]>([]);
  const [older, setOlder] = useState<Post[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [more, setMore] = useState(true);
  const action = useAction();
  useFocusEffect(
    useCallback(() => {
      return watchFeed(
        (value) => {
          setPosts(value);
          setLoading(false);
          setError('');
        },
        (e) => {
          setError(friendlyError(e));
          setLoading(false);
        },
      );
    }, []),
  );
  function onLikes(id: string, likes: Record<string, boolean>) {
    const update = (items: Post[]) =>
      items.map((item) => (item.id === id ? { ...item, likes } : item));
    setPosts(update);
    setOlder(update);
  }
  const ids = new Set(posts.map((post) => post.id));
  const data = [...posts, ...older.filter((post) => !ids.has(post.id))];
  if (loading)
    return (
      <Page>
        <Loading />
      </Page>
    );
  return (
    <Page scroll={false}>
      <FlatList
        data={data}
        keyExtractor={(item) => item.id}
        renderItem={({ item }) => <PostCard post={item} onLikes={onLikes} />}
        contentContainerStyle={{ padding: 20, paddingBottom: 30 }}
        ListHeaderComponent={
          <>
            <Heading title="Your community." subtitle="Small moments. Good conversations." />
            <Button title="Share a moment +" onPress={() => router.push('/create-post')} />
            <ErrorText message={error} />
            {error && <Button secondary title="Try again" onPress={retry} />}
          </>
        }
        ListEmptyComponent={
          <View style={styles.card}>
            <Text style={styles.name}>Start something good.</Text>
            <Text style={styles.copy}>There are no posts yet. Be the first to share.</Text>
          </View>
        }
        ListFooterComponent={
          <>
            <ErrorText message={action.error} />
            {data.length >= 30 && more && (
              <Button
                title="Load older posts"
                secondary
                busy={action.busy}
                onPress={() =>
                  action.run(async () => {
                    const page = await olderPosts(data[data.length - 1].id);
                    setMore(page.length === 30);
                    setOlder((current) => {
                      const seen = new Set(current.map((post) => post.id));
                      return [...current, ...page.filter((post) => !seen.has(post.id))];
                    });
                  })
                }
              />
            )}
          </>
        }
      />
    </Page>
  );
}
