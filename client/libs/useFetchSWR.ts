import { client } from './client';
import useSWR from 'swr';

const fetcher = async (url: string) =>
  await client.get(url).then((res) => res.data);

export const useFetch = (url: string) => {
  const { data, error, mutate } = useSWR(url, fetcher);

  return {
    data,
    isLoading: !error && !data,
    isError: error,
    mutate,
  };
};
