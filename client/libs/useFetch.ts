import { client } from './client';
import useSWR from 'swr';

const fetcher = async (url: string) =>
  await client.get(url).then((res) => res.data[0]);

export const useFetch = (url: string) => {
  const { data, error } = useSWR(url, fetcher);

  return {
    data,
    isLoading: !error && !data,
    isError: error,
  };
};
