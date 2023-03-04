import { isArticleEditAtom } from '@atoms/articleAtom';
import { useFetch } from '@libs/useFetchSWR';
import { ReportProps } from '@type/article';
import { ArticleApi } from 'api/aritlceApi';
import { useRouter } from 'next/router';
import { confirmAlert } from 'react-confirm-alert';
import { toast } from 'react-toastify';
import { useSetRecoilState } from 'recoil';

export const useQuestionContent = (articleId: string) => {
  const { data: article } = useFetch(`/api/articles/${articleId}`);
  const router = useRouter();
  const setArticleEdit = useSetRecoilState(isArticleEditAtom);

  const handleDelete = async () => {
    confirmAlert({
      message: '게시글을 삭제하시겠습니까?',
      buttons: [
        {
          label: 'YES',
          onClick: async () => {
            try {
              await ArticleApi.delete(articleId);
              toast.success('게시글이 삭제되었습니다.');
              router.replace(`/questions`);
            } catch (error) {
              console.log(error);
              toast.error('답변 삭제에 실패했습니다.');
            }
          },
        },
        {
          label: 'NO',
          onClick: () => {
            return;
          },
        },
      ],
    });
  };

  const handleToEdit = () => {
    setArticleEdit({
      isArticleEdit: true,
      title: article.title,
      content: article.content,
      articleId: articleId as string,
    });
    router.push(`/post`);
  };

  const handleReport = async (reportReason: ReportProps) => {
    try {
      await ArticleApi.report(articleId, reportReason);
      toast.success('신고가 정상적으로 처리되었습니다.');
      router.replace('/questions');
    } catch (err) {
      const typedError = err as Error;
      return {
        code: '',
        err: typedError.message,
      };
    }
  };

  return { handleDelete, handleToEdit, handleReport };
};
