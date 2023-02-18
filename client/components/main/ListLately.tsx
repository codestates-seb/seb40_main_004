import { faComment } from '@fortawesome/free-regular-svg-icons';
import { faHeart } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import Link from 'next/link';
import { useEffect, useState } from 'react';

import { changeGradeEmoji } from '@libs/changeGradeEmoji';
import { client } from '@libs/client';
import { elapsedTime } from '@libs/elapsedTime';
import { ArticleListProps } from '@type/article';

export const ListLately = () => {
  const [data, setData] = useState<ArticleListProps[] | null>(null);
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
      <div className="mb-6 ">
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
            {data &&
              data.slice(0, 5).map((article) => (
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
                              article.userInfo.grade
                                ? article.userInfo.grade
                                : '',
                            )}
                          </span>
                          <span>{article.userInfo.nickname}</span>
                        </div>
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
                              article.userInfo.grade
                                ? article.userInfo.grade
                                : '',
                            )}
                          </span>
                          <span>{article.userInfo.nickname}</span>
                        </div>
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
