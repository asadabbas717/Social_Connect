import { Image, Pressable, Text, View } from 'react-native';
import { router } from 'expo-router';
import type { Post } from '../domain/social';
import { useSession } from '../providers/Session';
import { toggleLike } from '../services/social';
import { useAction } from '../hooks/useAction';
import { Author } from './Author';
import { colors, ErrorText, styles } from './ui';

export function PostCard({
  post,
  onLikes,
}: {
  post: Post;
  onLikes: (id: string, likes: Record<string, boolean>) => void;
}) {
  const { user } = useSession();
  const { busy, run, error } = useAction();
  const likes = post.likes;
  const liked = !!user && likes[user.uid] === true;
  return (
    <View style={styles.card}>
      <Author uid={post.uid} timestamp={post.timestamp} />
      {!!post.text && (
        <Text selectable style={styles.copy}>
          {post.text}
        </Text>
      )}
      {!!post.imageUrl && (
        <Image
          source={{ uri: post.imageUrl }}
          style={styles.image}
          accessibilityLabel="Image attached to this post"
        />
      )}
      <View
        style={[
          styles.row,
          {
            justifyContent: 'space-between',
            borderTopWidth: 1,
            borderTopColor: colors.line,
            paddingTop: 8,
          },
        ]}
      >
        <Pressable
          disabled={busy}
          accessibilityRole="button"
          accessibilityLabel={liked ? 'Unlike post' : 'Like post'}
          accessibilityState={{ selected: liked, disabled: busy }}
          onPress={() =>
            run(async () => {
              onLikes(post.id, await toggleLike(post.id));
            })
          }
          style={{ minHeight: 48, justifyContent: 'center', paddingHorizontal: 8 }}
        >
          <Text style={{ color: liked ? colors.accent : colors.muted, fontWeight: '600' }}>
            {liked ? '♥' : '♡'} {Object.keys(likes).length} likes
          </Text>
        </Pressable>
        <Pressable
          accessibilityRole="button"
          onPress={() => router.push({ pathname: '/comments/[id]', params: { id: post.id } })}
          style={{ minHeight: 48, justifyContent: 'center', paddingHorizontal: 8 }}
        >
          <Text style={{ color: colors.accent, fontWeight: '600' }}>Comments →</Text>
        </Pressable>
      </View>
      <ErrorText message={error} />
    </View>
  );
}
