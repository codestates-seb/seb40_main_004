import Link from 'next/link';

import { faComment } from '@fortawesome/free-regular-svg-icons';
import { faHeart } from '@fortawesome/free-solid-svg-icons';
import { faCircleCheck as voidCheck } from '@fortawesome/free-regular-svg-icons';
import { faCircleCheck as solidCheck } from '@fortawesome/free-solid-svg-icons';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { ArticleListProps } from '@type/article';
import { elapsedTime } from '@libs/elapsedTime';

import { Pagination } from '@components/common/Pagination';
import { Loader } from '@components/common/Loader';

const className =
  'flex justify-center items-center my-20 text-main-gray w-full h-screen text-base';

export const QuestionList = ({
  response,
  isLoading,
  pageIndex,
  setPageIndex,
}: any) => {
  if (!isLoading && response && response.data.length)
    return (
      <main className="flex flex-col w-full divide-y min-h-screen">
        {response.data.map((article: ArticleListProps) => (
          <Link
            href={`/questions/${article.articleId}`}
            key={article.articleId}
          >
            <section className="py-4 space-y-4 hover:cursor-pointer hover:bg-gray-100">
              <article className="space-x-2">
                {article.isClosed ? (
                  <FontAwesomeIcon
                    icon={solidCheck}
                    className="fa-lg text-main-orange"
                  />
                ) : (
                  <FontAwesomeIcon icon={voidCheck} className="fa-lg" />
                )}

                <span className="text-lg font-bold hover:cursor-pointer">
                  {article?.title?.length >= 35
                    ? `${article?.title?.slice(0, 35)}...`
                    : article?.title}
                </span>
              </article>
              <section className="flex justify-between items-center">
                <article className="flex space-x-3">
                  <div className="flex">
                    <Link href={`/dashboard/${article?.userInfo?.userId}`}>
                      <span className="text-xs hover:cursor-pointer">
                        {article?.userInfo?.nickname}
                      </span>
                    </Link>
                  </div>
                  <div className="text-xs space-x-2">
                    {article?.tags?.map((tag, i) => (
                      <span key={i}>{i < 3 ? `#${tag.name}` : ''}</span>
                    ))}
                  </div>
                </article>
                <article className="flex gap-4">
                  <div className="flex">
                    <span className="text-xs">
                      {elapsedTime(article.createdAt)}
                    </span>
                  </div>
                  <div className="flex gap-2">
                    <FontAwesomeIcon icon={faHeart} size="xs" />
                    <span className="text-xs">{article.likes}</span>
                  </div>
                  <div className="flex gap-2">
                    <FontAwesomeIcon icon={faComment} size="xs" />
                    <span className="text-xs">{article.answerCount}</span>
                  </div>
                </article>
              </section>
            </section>
          </Link>
        ))}
        <div className="mx-auto mt-10">
          <Pagination
            setPageIndex={setPageIndex}
            totalPage={response?.pageInfo?.totalPages}
            pageIndex={pageIndex}
          />
        </div>
      </main>
    );
  else if (!isLoading && !response?.data.length)
    return <div className={className}>검색 결과를 찾을 수 없습니다.🥲</div>;
  else
    return (
      <div className={className}>
        <Loader />
      </div>
    );
};
