/*
 * 책임 작성자: 정하승
 * 최초 작성일: 2022-11-18
 * 최근 수정일: 2022-12-02(박혜정)
 * 개요: 질문 리스트를 표시합니다.
 */

import Link from 'next/link';
import { QuestionListProps } from '../../libs/interfaces';
import { useFetch } from '../../libs/useFetchSWR';
import { elapsedTime } from '../../libs/elapsedTime';
import { faComment } from '@fortawesome/free-regular-svg-icons';
import { faHeart } from '@fortawesome/free-solid-svg-icons';
import { faCircleCheck as voidCheck } from '@fortawesome/free-regular-svg-icons';
import { faCircleCheck as solidCheck } from '@fortawesome/free-solid-svg-icons';
import { useState } from 'react';
import { Pagination } from './Pagination';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faChevronDown } from '@fortawesome/free-solid-svg-icons';

export const QuestionList = () => {
  const [pageIndex, setPageIndex] = useState(1);
  const { data: response, isLoading } = useFetch(
    `/api/articles?page=${pageIndex}&size=10&category=QNA`,
  );

  const onClick = () => {
    alert('Coming Soon...😸');
  };

  if (!isLoading)
    return (
      <main className="flex flex-col w-full space-y-6">
        <section className="flex justify-between mb-3">
          <button
            className="border border-main-gray rounded-lg py-1.5 w-28"
            onClick={onClick}
          >
            {`최근순 `}
            <FontAwesomeIcon icon={faChevronDown} className="fa-xs" />
          </button>
          <Link href="/ask">
            <button className="bg-main-yellow hover:bg-main-orange rounded-lg py-1.5 w-28 transition-all">
              질문하기
            </button>
          </Link>
        </section>
        {response.data.map((article: QuestionListProps) => (
          <section className="h-16 border-b spac-y-3" key={article.articleId}>
            <article className="space-x-2">
              {article.isClosed ? (
                <FontAwesomeIcon
                  icon={solidCheck}
                  className="fa-lg text-main-orange"
                />
              ) : (
                <FontAwesomeIcon icon={voidCheck} className="fa-lg" />
              )}

              <Link href={`/questions/${article.articleId}`}>
                <span className="text-lg font-bold hover:cursor-pointer">
                  {article.title}
                </span>
              </Link>
            </article>
            <section className="flex justify-between items-center">
              <article className="flex space-x-3">
                <div className="flex">
                  <Link href={`/dashboard/${article.userInfo.userId}`}>
                    <span className="text-xs hover:cursor-pointer">
                      {article.userInfo.nickname}
                    </span>
                  </Link>
                </div>
                <div className="text-xs space-x-2">
                  {article.tags.map((tag) => (
                    <span key={tag.name} className="">
                      #{tag.name}
                    </span>
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
        <div className="mx-auto pt-4">
          <Pagination
            setPageIndex={setPageIndex}
            totalPage={response.pageInfo.totalPages}
            pageIndex={pageIndex}
          />
        </div>
      </main>
    );
  else return <div className="">Loading...</div>;
};
