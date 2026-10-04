import { Stack, type ErrorBoundaryProps } from 'expo-router';
import { StatusBar } from 'expo-status-bar';
import { SafeAreaProvider } from 'react-native-safe-area-context';
import { SessionProvider, useSession } from '../providers/Session';
import { Button, colors, Heading, Loading, Page } from '../components/ui';

export function ErrorBoundary({ retry }: ErrorBoundaryProps) {
  return (
    <Page>
      <Heading title="Something went wrong." subtitle="Please try opening this screen again." />
      <Button title="Try again" onPress={retry} />
    </Page>
  );
}

function Navigation() {
  const { user, loading } = useSession();
  if (loading) return <Loading />;
  return (
    <Stack
      screenOptions={{
        headerStyle: { backgroundColor: colors.paper },
        headerTintColor: colors.ink,
        headerShadowVisible: false,
        contentStyle: { backgroundColor: colors.paper },
      }}
    >
      <Stack.Screen name="index" options={{ headerShown: false }} />
      <Stack.Protected guard={!user}>
        <Stack.Screen name="sign-in" options={{ title: 'Social Connect' }} />
      </Stack.Protected>
      <Stack.Protected guard={!!user}>
        <Stack.Screen name="(tabs)" options={{ headerShown: false }} />
        <Stack.Screen name="create-post" options={{ title: 'New post', presentation: 'modal' }} />
        <Stack.Screen name="edit-profile" options={{ title: 'Edit profile' }} />
        <Stack.Screen name="comments/[id]" options={{ title: 'Conversation' }} />
      </Stack.Protected>
    </Stack>
  );
}
export default function RootLayout() {
  return (
    <SafeAreaProvider>
      <SessionProvider>
        <StatusBar style="dark" />
        <Navigation />
      </SessionProvider>
    </SafeAreaProvider>
  );
}
