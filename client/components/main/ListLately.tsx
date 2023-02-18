import { faComment } from '@fortawesome/free-regular-svg-icons';
import { faHeart } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import Link from 'next/link';

import { changeGradeEmoji } from '@libs/changeGradeEmoji';
import { elapsedTime } from '@libs/elapsedTime';
import { ArticleListProps } from '@type/article';
import { useFetch } from '@libs/useFetchSWR';
import { Loader } from '@components/common/Loader';

type ArticleCardProps = {
  article: ArticleListProps;
};

export const ArticleCard = ({ article }: ArticleCardProps) => {
  return (
    <div
      className="w-[492px] h-18 border-b mb-8 hover:bg-gray-100 cursor-pointer"
      key={article.articleId}
    >
      <div className="mb-4">
        <Link href={`/questions/${article.articleId}`}>
          <span className="text-lg font-bold hover:cursor-pointer">
            {article.title.length > 35
              ? `${article.title.slice(0, 35)}...`
              : article.title}
          </span>
        </Link>
      </div>
      <div className="flex justify-between items-center mb-2">
        <div className="flex">
          <Link href={`/dashboard/${article.userInfo.userId}`}>
            <div className="text-xs flex gap-2 hover:cursor-pointer">
              <span>
                {changeGradeEmoji(
                  article.userInfo.grade ? article.userInfo.grade : '',
                )}
              </span>
              <span>{article.userInfo.nickname}</span>
            </div>
          </Link>
        </div>
        <div className="flex gap-4">
          <div className="flex">
            <span className="text-xs">{elapsedTime(article.createdAt)}</span>
          </div>
          <div className="flex gap-2">
            <FontAwesomeIcon icon={faHeart} size="xs" />
            <span className="text-xs">{article.likes}</span>
          </div>
          <div className="flex gap-2">
            <FontAwesomeIcon icon={faComment} size="xs" />
            <span className="text-xs">{article.answerCount}</span>
          </div>
        </div>
      </div>
    </div>
  );
};

export const ListLately = () => {
  const URL =
    '/api/articles?category=QNA&keyword=null&target=null&sort=desc&page=1&size=10';

  const { data, isLoading } = useFetch(URL);

  return !isLoading ? (
    <section className="w-[1163px] mx-auto rounded-2xl bg-white py-10 px-14 space-y-6">
      <div>
        <Link href="/questions">
          <div className="text-2xl mr-2 font-bold hover:cursor-pointer hover:opacity-50 inline-block select-none">
            ❓ 최근 질문
          </div>
        </Link>
        <Link href="/questions">
          <span className="text-xs hover:cursor-pointer hover:opacity-40">
            더보기 ＞
          </span>
        </Link>
        <Link href="/post">
          <span className="text-xs ml-4 hover:cursor-pointer hover:opacity-40">
            📝 질문 작성 ＞
          </span>
        </Link>
      </div>
      {
        <div className="flex justify-between">
          <div>
            {data.data.slice(0, 5).map((article: ArticleListProps) => (
              <ArticleCard article={article} key={article.articleId} />
            ))}
          </div>
          <div>
            {data.data.slice(5).map((article: ArticleListProps) => (
              <ArticleCard article={article} key={article.articleId} />
            ))}
          </div>
        </div>
      }
    </section>
  ) : (
    <section className="w-full h-full flex justify-center items-center">
      <Loader />
    </section>
  );
};
