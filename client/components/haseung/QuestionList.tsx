/*
 * 책임 작성자: 정하승
 * 최초 작성일: 2022-11-18
 * 최근 수정일: 2022-11-30
 * 개요: 질문 리스트를 표시합니다.
 */

import Link from 'next/link';
import { QuestionListProps } from '../../libs/interfaces';
import { useFetch } from '../../libs/useFetchSWR';
import { CreatedDate } from '../hyejung/CreatedDate';
import { QuestionTitle } from '../hyejung/QuestionTitle';
import { TagList } from '../hyejung/TagList';
import { UserNickname } from '../hyejung/UserNickname';

export const QuestionList = () => {
  //전체글 조회
  const { data } = useFetch(
    `/api/articles?category=QNA&keyword=&target=&sort=&page=1&size=20`,
  );

  return (
    <section className="flex flex-col w-full mt-10 space-y-3">
      <Link href="/ask">
        <button className="bg-main-yellow hover:bg-main-orange w-28 mb-5 rounded-2xl py-1 ml-96">
          질문하기
        </button>
      </Link>
      {data?.data.map((item: QuestionListProps) => (
        <article className="whitespace-nowrap ml-96">
          <QuestionTitle title={item?.title} />
          <article className="flex justify-start space-x-2 mb-4">
            <UserNickname
              nickname={item?.userInfo.nickname}
              userId={item?.userInfo.userId}
              grade="BRONZE"
            />
            <div className="flex flex-col justify-center">
              <CreatedDate createdAt={item?.createdAt} />
            </div>
          </article>
          <TagList tags={item?.tags} />
        </article>
      ))}
    </section>
  );
};
