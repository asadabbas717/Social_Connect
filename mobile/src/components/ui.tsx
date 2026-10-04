import {
  ActivityIndicator,
  Image,
  KeyboardAvoidingView,
  Platform,
  Pressable,
  ScrollView,
  StyleSheet,
  Text,
  TextInput,
  View,
  type TextInputProps,
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import type { PropsWithChildren } from 'react';

export const colors = {
  ink: '#182D29',
  muted: '#52655F',
  accent: '#196B52',
  light: '#EDF5F0',
  paper: '#FAFBF8',
  line: '#D9E3DC',
  error: '#A12C32',
};
export function Page({ children, scroll = true }: PropsWithChildren<{ scroll?: boolean }>) {
  return (
    <SafeAreaView edges={['bottom', 'left', 'right']} style={styles.page}>
      <KeyboardAvoidingView
        style={{ flex: 1 }}
        behavior={Platform.OS === 'ios' ? 'padding' : undefined}
      >
        {scroll ? (
          <ScrollView contentContainerStyle={styles.body} keyboardShouldPersistTaps="handled">
            {children}
          </ScrollView>
        ) : (
          children
        )}
      </KeyboardAvoidingView>
    </SafeAreaView>
  );
}
export function Heading({ title, subtitle }: { title: string; subtitle?: string }) {
  return (
    <View style={{ gap: 8, marginBottom: 20 }}>
      <Text style={styles.title}>{title}</Text>
      {subtitle && <Text style={styles.subtitle}>{subtitle}</Text>}
    </View>
  );
}
export function Button({
  title,
  onPress,
  busy = false,
  secondary = false,
  disabled = false,
}: {
  title: string;
  onPress: () => void;
  busy?: boolean;
  secondary?: boolean;
  disabled?: boolean;
}) {
  return (
    <Pressable
      accessibilityRole="button"
      accessibilityLabel={title}
      accessibilityState={{ disabled: busy || disabled, busy }}
      onPress={onPress}
      disabled={busy || disabled}
      style={({ pressed }) => [
        styles.button,
        secondary && styles.secondary,
        (pressed || busy || disabled) && { opacity: 0.65 },
      ]}
    >
      {busy ? (
        <ActivityIndicator color={secondary ? colors.accent : 'white'} />
      ) : (
        <Text style={[styles.buttonText, secondary && { color: colors.accent }]}>{title}</Text>
      )}
    </Pressable>
  );
}
export function Field({ label, ...props }: TextInputProps & { label: string }) {
  return (
    <View style={{ gap: 7, marginBottom: 16 }}>
      <Text style={styles.label}>{label}</Text>
      <TextInput
        accessibilityLabel={label}
        placeholderTextColor={colors.muted}
        {...props}
        style={[
          styles.input,
          props.multiline && { minHeight: 100, textAlignVertical: 'top' },
          props.style,
        ]}
      />
    </View>
  );
}
export function ErrorText({ message }: { message: string }) {
  return message ? (
    <Text accessibilityRole="alert" accessibilityLiveRegion="polite" style={styles.error}>
      {message}
    </Text>
  ) : null;
}
export function Loading() {
  return (
    <View style={styles.center}>
      <ActivityIndicator size="large" color={colors.accent} accessibilityLabel="Loading" />
    </View>
  );
}
export function Avatar({ url, name, size = 44 }: { url?: string; name: string; size?: number }) {
  return url ? (
    <Image
      source={{ uri: url }}
      accessibilityLabel={`${name}'s profile image`}
      style={{ width: size, height: size, borderRadius: size / 2 }}
    />
  ) : (
    <View style={[styles.avatar, { width: size, height: size, borderRadius: size / 2 }]}>
      <Text style={{ color: colors.accent, fontWeight: '700', fontSize: size / 3 }}>
        {name.slice(0, 1).toUpperCase() || 'S'}
      </Text>
    </View>
  );
}
export const styles = StyleSheet.create({
  page: { flex: 1, backgroundColor: colors.paper },
  body: { padding: 22, paddingBottom: 36 },
  title: { fontSize: 30, fontWeight: '700', color: colors.ink, letterSpacing: -0.8 },
  subtitle: { color: colors.muted, fontSize: 16, lineHeight: 24 },
  label: { color: colors.ink, fontWeight: '600', fontSize: 14 },
  input: {
    backgroundColor: 'white',
    borderWidth: 1,
    borderColor: colors.line,
    borderRadius: 12,
    padding: 14,
    color: colors.ink,
    fontSize: 16,
    minHeight: 50,
  },
  button: {
    minHeight: 50,
    backgroundColor: colors.accent,
    padding: 14,
    borderRadius: 12,
    alignItems: 'center',
    justifyContent: 'center',
    marginBottom: 10,
  },
  secondary: { backgroundColor: colors.light, borderWidth: 1, borderColor: colors.line },
  buttonText: { color: 'white', fontWeight: '600', fontSize: 16 },
  error: { color: colors.error, lineHeight: 22, marginBottom: 14 },
  center: { flex: 1, alignItems: 'center', justifyContent: 'center', padding: 30 },
  card: {
    backgroundColor: 'white',
    borderWidth: 1,
    borderColor: colors.line,
    borderRadius: 18,
    padding: 18,
    gap: 14,
    marginBottom: 16,
  },
  row: { flexDirection: 'row', alignItems: 'center', gap: 12 },
  avatar: { backgroundColor: colors.light, alignItems: 'center', justifyContent: 'center' },
  name: { color: colors.ink, fontSize: 16, fontWeight: '600' },
  small: { color: colors.muted, fontSize: 13 },
  copy: { color: colors.ink, fontSize: 16, lineHeight: 24 },
  image: { width: '100%', height: 240, borderRadius: 12, backgroundColor: colors.light },
  link: { color: colors.accent, fontWeight: '600', paddingVertical: 14 },
  divider: { height: 1, backgroundColor: colors.line, marginVertical: 16 },
});
