/*
 * 책임 작성자: 박연우
 * 최초 작성일: 2022-11-18
 * 최근 수정일: 2022-12-02
 */

import { faComment } from '@fortawesome/free-regular-svg-icons';
import { faHeart } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import Link from 'next/link';
import { useEffect, useState } from 'react';
import { articleList } from '../../interfaces';
import { client } from '../../libs/client';
import { elapsedTime } from '../../libs/elapsedTime';

export const ListLately = () => {
  const [data, setData] = useState<articleList[] | null>(null);
  const getList = async () => {
    const res = await client.get(
      '/api/articles?category=QNA&keyword=null&target=null&sort=desc&page=1&size=10',
    );
    setData(res.data.data);
  };

  useEffect(() => {
    getList();
  }, []);

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
                        {article.title.length > 35
                          ? `${article.title.slice(0, 35)}...`
                          : article.title}
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
                    <Link href={`/questions/${article.articleId}`}>
                      <span className="text-lg font-bold hover:cursor-pointer">
                        {article.title.length > 35
                          ? `${article.title.slice(0, 35)}...`
                          : article.title}
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
