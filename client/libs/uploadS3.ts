import axios from 'axios';
import { client } from './client';

export const getFileUrl = async () => {
  return await (
    await client.get('/api/files')
  ).data;
};

export const uploadImg = async (url: string, file: File | null) => {
  try {
    await axios.put(url, file, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
  } catch (error) {
    console.error(error);
  }
};
