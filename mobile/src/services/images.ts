import * as ImagePicker from 'expo-image-picker';
import { validateImage, type PickedImage } from '../domain/social';

export async function chooseImage(camera = false): Promise<PickedImage | undefined> {
  if (camera) {
    const permission = await ImagePicker.requestCameraPermissionsAsync();
    if (!permission.granted)
      throw new Error(
        'Camera access is required to take a photo. You can also choose one from your library.',
      );
  }
  const options: ImagePicker.ImagePickerOptions = {
    mediaTypes: ['images'],
    allowsEditing: true,
    quality: 0.8,
  };
  const result = camera
    ? await ImagePicker.launchCameraAsync(options)
    : await ImagePicker.launchImageLibraryAsync(options);
  if (result.canceled) return undefined;
  const asset = result.assets[0];
  const image = {
    uri: asset.uri,
    mimeType: asset.mimeType ?? 'image/jpeg',
    size: asset.fileSize ?? 0,
  };
  validateImage(image);
  return image;
}
