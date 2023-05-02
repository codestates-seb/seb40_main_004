import { useRouter } from 'next/router';

export const useGetArticleId = () => {
  const router = useRouter();
  const { articleId } = router.query;

  return { articleId };
};
