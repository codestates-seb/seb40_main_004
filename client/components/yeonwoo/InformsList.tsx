/*
 * 책임 작성자: 박연우
 * 최초 작성일: 2022-12-11
 * 최근 수정일: 2022-12-11
 * 개요: 정보글 리스트를 표시합니다.
 */

import Link from 'next/link';
import { QuestionListProps } from '../../libs/interfaces';
import { elapsedTime } from '../../libs/elapsedTime';
import { faComment } from '@fortawesome/free-regular-svg-icons';
import { faHeart } from '@fortawesome/free-solid-svg-icons';
import { faCircleCheck as voidCheck } from '@fortawesome/free-regular-svg-icons';
import { faCircleCheck as solidCheck } from '@fortawesome/free-solid-svg-icons';

import { Pagination } from '../haseung/Pagination';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { Loader } from '../common/Loader';

export const InformsList = ({
  response,
  isLoading,
  pageIndex,
  setPageIndex,
}: any) => {
  if (!isLoading && response && response.data.length)
    return (
      <main className="flex flex-col w-full divide-y min-h-screen">
        {response.data.map((article: QuestionListProps) => (
          <section className="py-4 space-y-4 " key={article.articleId}>
            <article className="space-x-2">
              <Link href={`/informations/${article.articleId}`}>
                <span className="text-lg font-bold hover:cursor-pointer">
                  {article?.title?.length >= 35
                    ? `${article?.title?.slice(0, 35)}...`
                    : article?.title}
                </span>
              </Link>
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
                  <span className="text-xs">{article.commentCount}</span>
                </div>
              </article>
            </section>
          </section>
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
    return (
      <div className="flex justify-center items-center my-20 text-main-gray w-full h-screen text-base">
        검색 결과를 찾을 수 없습니다.🥲
      </div>
    );
  else
    return (
      <div className="flex justify-center items-center my-20 text-main-gray w-full h-screen text-base">
        <Loader />
      </div>
    );
};
