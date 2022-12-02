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
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { useState } from 'react';
import { Pagination } from './Pagination';

export const QuestionList = () => {
  const [pageIndex, setPageIndex] = useState(1);
  const { data: response, isLoading } = useFetch(
    `/api/articles?page=${pageIndex}&size=10&category=QNA`,
  );

  if (!isLoading)
    return (
      <main className="flex flex-col w-full space-y-6">
        <Link href="/ask">
          <button className="bg-main-yellow hover:bg-main-orange w-28 mb-5 rounded-2xl py-1 ml-96">
            질문하기
          </button>
        </Link>
        {response.data.map((article: QuestionListProps) => (
          <section className="h-16 border-b space-y-3" key={article.articleId}>
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
        <div className="mx-auto">
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

/*
페이지네이션 요구사항
1. 페이지번호를 누르면 해당 번호가 인덱스가 되어서 요청을 새롭게 한다.
2. 버튼은 5개씩 보여준다.
3. 5번째에 다다라서 다음 버튼을 누르면, 그다음 6~10까지의 버튼이 나온다.
*/
