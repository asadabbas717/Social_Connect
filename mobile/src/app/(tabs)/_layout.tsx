import { Tabs } from 'expo-router';
import { Text } from 'react-native';
import { colors } from '../../components/ui';
export default function TabLayout() {
  return (
    <Tabs
      screenOptions={{
        headerStyle: { backgroundColor: colors.paper },
        headerTintColor: colors.ink,
        headerShadowVisible: false,
        tabBarActiveTintColor: colors.accent,
        tabBarInactiveTintColor: colors.muted,
        tabBarStyle: { backgroundColor: colors.paper },
        tabBarLabelStyle: { fontSize: 12, fontWeight: '600' },
      }}
    >
      <Tabs.Screen
        name="index"
        options={{
          title: 'Loopnest',
          tabBarLabel: 'Feed',
          tabBarIcon: ({ color }) => <Text style={{ color, fontSize: 22 }}>⌂</Text>,
        }}
      />
      <Tabs.Screen
        name="profile"
        options={{
          title: 'Your profile',
          tabBarLabel: 'Profile',
          tabBarIcon: ({ color }) => <Text style={{ color, fontSize: 22 }}>○</Text>,
        }}
      />
      <Tabs.Screen
        name="settings"
        options={{
          title: 'Settings',
          tabBarIcon: ({ color }) => <Text style={{ color, fontSize: 22 }}>⚙</Text>,
        }}
      />
    </Tabs>
  );
}
