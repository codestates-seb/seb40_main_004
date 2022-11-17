// import { useFetch } from '../../../libs/useFetch';
// import { ArticleDetail } from '../../../mocks/types';

export const AnswerMainText = () => {
  //   const { data, isLoading } = useFetch('/api/articles');
  //   const content: ArticleDetail = data?.article;
  //   if (isLoading) return <div>로딩중~</div>;
  return (
    <main>
      <p>
        Answer 컴포넌트는 하드코딩된 데이터가 출력중입니다~~ msw 에서 요청이
        제대로 보내지지 않는 경우가 있는데 일단 무한 새로고침해서 쓰고있어요..ㅜ
      </p>
    </main>
  );
};
