/*
 * 책임 작성자: 박연우
 * 최초 작성일: 2022-11-18
 * 최근 수정일: 2022-11-29
 */

import { faComment } from '@fortawesome/free-regular-svg-icons';
import { faHeart } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import Link from 'next/link';
import axios from 'axios';
import { useEffect, useState } from 'react';
import { articleList } from '../../interfaces';

export const ListLately = () => {
  const [data, setData] = useState<articleList[] | null>(null);
  const getList = async () => {
    const res = await axios.get(
      '/api/articles?category=INFO&keyword=null&target=null&sort=desc&page=1&size=10',
    );
    setData(res.data.data);
  };

  useEffect(() => {
    getList();
  }, []);

  const howManyTimesAgo = (createdAtUTC: string) => {
    const date = new Date(createdAtUTC);
    const createdAt = date.getTime();
    const diffSeconds = Math.round((Date.now() - createdAt) / 1000);

    if (diffSeconds < 60) {
      if (diffSeconds === 1) return '1 sec ago';
      return `${diffSeconds}초 전`;
    }

    const diffMinutes = Math.round(diffSeconds / 60);

    if (diffMinutes < 60) {
      if (diffMinutes === 1) return '1 sec ago';
      return `${diffMinutes}분 전`;
    }

    const diffHours = Math.round(diffMinutes / 60);

    if (diffHours < 24) {
      if (diffHours === 1) return '1 hour ago';
      return `${diffHours}시간 전`;
    }

    const diffDays = Math.round(diffHours / 24);

    if (diffDays < 3) {
      if (diffDays === 1) return '하루 전';
      return '이틀 전';
    }

    const dateCreatedAt = new Date(createdAt - 32400000);

    return `${dateCreatedAt.getFullYear()}년 ${
      dateCreatedAt.getMonth() + 1
    }월 ${dateCreatedAt.getDate()}일 ${
      dateCreatedAt.getHours() < 12 ? '오전' : '오후'
    } ${
      dateCreatedAt.getHours() > 12
        ? dateCreatedAt.getHours() - 12
        : dateCreatedAt.getHours()
    }시 ${dateCreatedAt.getMinutes()}분`;
  };

  return (
    <>
      <div className="mb-6">
        <span className="text-2xl mr-6 font-bold">❓ 최근 질문</span>
        <Link href="/questions">
          <span className="text-xs hover:cursor-pointer">더보기 ＞</span>
        </Link>
      </div>
      {
        <div className="flex justify-between">
          <div>
            {data &&
              data.slice(0, 5).map((article) => (
                <div
                  className="w-[492px] h-16 border-b mb-8"
                  key={article.articleId}
                >
                  <div className="mb-4">
                    <Link href={`/questions/${article.articleId}`}>
                      <span className="text-lg font-bold hover:cursor-pointer">
                        {article.title}
                      </span>
                    </Link>
                  </div>
                  <div className="flex justify-between items-center">
                    <div className="flex">
                      <Link href={`/dashboard/${article.userInfo.userId}`}>
                        <span className="text-xs hover:cursor-pointer">
                          {article.userInfo.nickname}
                        </span>
                      </Link>
                    </div>
                    <div className="flex gap-4">
                      <div className="flex">
                        <span className="text-xs">
                          {howManyTimesAgo(article.createdAt)}
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
                    </div>
                  </div>
                </div>
              ))}
          </div>
          <div>
            {data &&
              data.slice(5).map((article) => (
                <div
                  className="w-[492px] h-16 border-b mb-8"
                  key={article.articleId}
                >
                  <div className="mb-4">
                    <span className="text-lg font-bold">{article.title}</span>
                  </div>
                  <div className="flex justify-between items-center">
                    <div className="flex">
                      <span className="text-xs">
                        {article.userInfo.nickname}
                      </span>
                    </div>
                    <div className="flex gap-4">
                      <div className="flex">
                        <span className="text-xs">
                          {howManyTimesAgo(article.createdAt)}
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
                    </div>
                  </div>
                </div>
              ))}
          </div>
        </div>
      }
    </>
  );
};
