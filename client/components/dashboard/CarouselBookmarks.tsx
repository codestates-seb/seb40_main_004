import * as React from 'react';
import { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { wrap } from 'popmotion';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {
  faChevronLeft,
  faChevronRight,
  faComment,
  faHeart,
} from '@fortawesome/free-solid-svg-icons';
import { useRouter } from 'next/router';
import Link from 'next/link';
import { client } from '../../libs/client';
import { changeTagPrettier } from '../../libs/changeTagPrettier';

const variants = {
  enter: (direction: number) => {
    return {
      x: direction > 0 ? 1000 : -1000,
      opacity: 0,
    };
  },
  center: {
    zIndex: 1,
    x: 0,
    opacity: 1,
  },
  exit: (direction: number) => {
    return {
      zIndex: 0,
      x: direction < 0 ? 1000 : -1000,
      opacity: 0,
    };
  },
};

/**
 * Experimenting with distilling swipe offset and velocity into a single variable, so the
 * less distance a user has swiped, the more velocity they need to register as a swipe.
 * Should accomodate longer swipes and short flicks without having binary checks on
 * just distance thresholds and velocity > 0.
 */
const swipeConfidenceThreshold = 10000;
const swipePower = (offset: number, velocity: number) => {
  return Math.abs(offset) * velocity;
};

interface IBookmarkArticle {
  articleId: number;
  category: string;
  title: string;
  clicks: number;
  likes: number;
  isClosed: false;
  tags: [
    {
      tagId: number;
      name: string;
    },
  ];
  commentCount: number;
  answerCount: number;
  createdAt: string;
  lastModifiedAt: string;
  userInfo: {
    userId: number;
    nickname: string;
    grade: string;
  };
  avatar: {
    avatarId: number;
    filename: string;
    remotePath: string;
  };
}

export const CarouselBookmarks = () => {
  const router = useRouter();
  const [userId, setUserId] = useState<string | string[] | undefined>('');
  const [articles, setArticles] = useState<IBookmarkArticle[] | []>([]);
  const getReview = async () =>
    await client
      .get(
        `/api/articles?category=QNA&keyword=${userId}&target=bookmark&sort=desc&page=1&size=50`,
      )
      .then((res) => setArticles(res.data.data))
      .catch((error) => console.log(error));

  useEffect(() => {
    setUserId(router.query.userId);
  });

  useEffect(() => {
    getReview();
  }, [userId]);
  const [[page, direction], setPage] = useState([0, 0]);

  // We only have 3 images, but we paginate them absolutely (ie 1, 2, 3, 4, 5...) and
  // then wrap that within 0-2 to find our image ID in the array below. By passing an
  // absolute page index as the `motion` component's `key` prop, `AnimatePresence` will
  // detect it as an entirely new image. So you can infinitely paginate as few as 1 images.
  const articleIndex = wrap(0, Math.ceil(articles.length / 2) * 2, page);

  const paginate = (newDirection: number) => {
    setPage([page + newDirection, newDirection]);
  };

  return (
    <div className="relative w-full h-[440px]">
      {articles.length !== 0 ? (
        <>
          <AnimatePresence initial={false} custom={direction}>
            <motion.div
              key={page}
              className="w-full h-[440px] absolute px-24 flex-col"
              custom={direction}
              variants={variants}
              initial="enter"
              animate="center"
              exit="exit"
              transition={{
                x: { type: 'spring', stiffness: 300, damping: 30 },
              }}
              drag="x"
              dragConstraints={{ left: 0, right: 0 }}
              dragElastic={1}
              onDragEnd={(e, { offset, velocity }) => {
                const swipe = swipePower(offset.x, velocity.x);

                if (swipe < -swipeConfidenceThreshold) {
                  paginate(2);
                } else if (swipe > swipeConfidenceThreshold) {
                  paginate(-2);
                }
              }}
            >
              {articles.slice(articleIndex, articleIndex + 2).map((article) => (
                <motion.div
                  key={article.articleId}
                  className="bg-main-yellow bg-opacity-20 w-[793px] h-[190px] rounded-2xl p-8 relative mb-[72px]"
                >
                  <div className="flex justify-between items-baseline">
                    <Link href={`/questions/${article.articleId}`}>
                      <div>
                        <span className="text-lg text-main-orange">B. </span>
                        <span className="hover:cursor-pointer text-lg">
                          {article.title.length > 30
                            ? `${article.title.slice(0, 30)}...`
                            : article.title}
                        </span>
                      </div>
                    </Link>
                    <div className="flex gap-4">
                      <div className="flex gap-2">
                        <FontAwesomeIcon icon={faHeart} size="sm" />
                        <span className="text-xs">{article.likes}</span>
                      </div>
                      <div className="flex gap-2">
                        <FontAwesomeIcon icon={faComment} size="sm" />
                        <span className="text-xs">{article.answerCount}</span>
                      </div>
                    </div>
                  </div>
                  <div className="flex justify-end mb-2">
                    <span className="text-xs text-main-gray">{`${new Date(
                      article.createdAt,
                    ).getFullYear()}년 ${
                      new Date(article.createdAt).getMonth() + 1
                    }월 ${new Date(article.createdAt).getDate()}일 ${
                      new Date(article.createdAt).getHours() < 12
                        ? '오전'
                        : '오후'
                    } ${
                      new Date(article.createdAt).getHours() > 12
                        ? new Date(article.createdAt).getHours() - 12
                        : new Date(article.createdAt).getHours()
                    }시 ${new Date(article.createdAt).getMinutes()}분`}</span>
                  </div>
                  <div className="flex justify-end gap-4 items-end h-16">
                    {article.tags.map((tag) => (
                      <button
                        className="bg-main-yellow rounded-full py-[6px] w-32 text-xs"
                        key={tag.tagId}
                      >
                        {changeTagPrettier(tag.name)}
                      </button>
                    ))}
                  </div>
                </motion.div>
              ))}
            </motion.div>
          </AnimatePresence>
          <div
            className="absolute top-[45%] z-10 right-0 hover:cursor-pointer"
            onClick={() => paginate(2)}
          >
            <FontAwesomeIcon icon={faChevronRight} size="2xl" />
          </div>
          <div
            className="absolute top-[45%] z-10 left-0 hover:cursor-pointer"
            onClick={() => paginate(-2)}
          >
            <FontAwesomeIcon icon={faChevronLeft} size="2xl" />
          </div>
        </>
      ) : (
        <div className="w-full h-full flex justify-center items-center">
          <span>등록된 북마크가 아직 없습니다</span>
        </div>
      )}
    </div>
  );
};
