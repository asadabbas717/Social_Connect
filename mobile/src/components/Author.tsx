import { useEffect, useState } from 'react';
import { Text, View } from 'react-native';
import { readProfile } from '../services/social';
import type { Profile } from '../domain/social';
import { Avatar, styles } from './ui';

export function Author({ uid, timestamp }: { uid: string; timestamp?: number | null }) {
  const [loaded, setLoaded] = useState<{ uid: string; profile: Profile } | null>(null);
  useEffect(() => {
    let active = true;
    readProfile(uid)
      .then((value) => {
        if (active) setLoaded({ uid, profile: value });
      })
      .catch(() => {
        if (active) setLoaded(null);
      });
    return () => {
      active = false;
    };
  }, [uid]);
  const profile = loaded?.uid === uid ? loaded.profile : null;
  const name = profile?.name || 'Community member';
  return (
    <View style={styles.row}>
      <Avatar name={name} />
      <View>
        <Text style={styles.name}>{name}</Text>
        {timestamp !== undefined && (
          <Text style={styles.small}>
            {timestamp ? new Date(timestamp).toLocaleDateString() : 'Sending…'}
          </Text>
        )}
      </View>
    </View>
  );
}
