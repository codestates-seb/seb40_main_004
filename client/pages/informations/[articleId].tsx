import { GetServerSideProps, NextPage } from 'next';
import { Header } from '../../components/common/Header';
import { Footer } from '../../components/common/Footer';
import { BtnTopDown } from '../../components/common/BtnTopDown';
import { Seo } from '../../components/common/Seo';
import axios from 'axios';
import { ArticleDetail } from '../../types/article';
import { SWRConfig } from 'swr';
import { QuestionContent } from '../../components/questions/question-detail/QuestionContent/QuestionContent';

type QuestionDetailProps = {
  articleId: string;
  article: ArticleDetail;
};

const QuestionDetail: NextPage<QuestionDetailProps> = ({
  articleId,
  article: articleData,
}) => {
  console.log(articleData);
  return (
    <>
      <Seo title={articleData.title} />
      <Header />
      <main className="max-w-[900px] mx-auto min-h-[80vh] bg-white  p-8 md:p-16 shadow-sm border-[1px] border-gray-200">
        <BtnTopDown />
        <QuestionContent articleId={articleId} article={articleData} />
      </main>
      <Footer />
    </>
  );
};

const Page: NextPage<{
  article: ArticleDetail;
  id: string;
}> = ({ article, id }) => {
  const keyArticle = `/articles/${id}`;
  return (
    // 질문 본문에 대한 캐시 초기값 설정
    <SWRConfig
      value={{
        fallback: {
          [keyArticle]: {
            article,
          },
        },
      }}
    >
      <QuestionDetail articleId={id} article={article} />
    </SWRConfig>
  );
};

export const getServerSideProps: GetServerSideProps = async (ctx) => {
  const id = ctx.params?.articleId;
  const BASE_URL = process.env.NEXT_PUBLIC_API_URL;

  // 게시글 본문 요청
  const resArticle = await axios.get(`${BASE_URL}/articles/${id}`);
  const article = resArticle.data;

  // article 데이터가 존재하지 않으면 일단 404 페이지로 이동
  if (!article) {
    return {
      redirect: {
        destination: '/404',
        permanent: false,
      },
    };
  }
  return {
    props: {
      article,
      id,
    },
  };
};

export default Page;
