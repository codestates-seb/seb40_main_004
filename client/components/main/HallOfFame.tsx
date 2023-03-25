import Image from 'next/image';
import Link from 'next/link';
import { useEffect, useState } from 'react';

import { changeGradeEmoji } from '@libs/changeGradeEmoji';
import { client } from '@libs/client';

import { Avatar } from '@type/user';
export interface RankList {
  userId: number;
  nickname: string;
  infoMessage: string | null;
  point: number;
  grade: string | null;
  jobType: string | null;
  articleCount: number;
  likeCount: number;
  answerCount: number;
  rank: number;
  avatar: Avatar | null;
}

export const HallOfFame = () => {
  const [ranks, setRanks] = useState<RankList[] | null>(null);
  const [errorAvatarId, setIsErrorAvatarId] = useState<(number | undefined)[]>(
    [],
  );

  const getRanks = async () => {
    try {
      const res = await client.get('/api/users/ranks?page=1&size=8');
      setRanks(res.data.data);
    } catch (error) {
      console.log(error);
    }
  };

  useEffect(() => {
    getRanks();
  }, []);

  if (!ranks)
    return (
      <section className="h-full flex justify-center items-center">
        데이터 요청에 실패하였습니다.
      </section>
    );
  return (
    <>
      <div>
        <span className="text-2xl font-bold">🏆 명예의 전당</span>
      </div>
      <div>
        {ranks &&
          ranks.map((rank) => (
            <div
              className="w-[350px] h-[60px] flex gap-[10px] items-center border-b mt-4"
              key={rank.userId}
            >
              <div className="w-[14px]">
                <span className="text-lg font-bold">{rank.rank}</span>
              </div>
              <div className="w-[326px] flex gap-2">
                <Link href={`/dashboard/${rank.userId}`}>
                  <div className="w-[45px] h-[45px] rounded-full overflow-hidden hover:cursor-pointer">
                    {errorAvatarId.includes(rank.avatar?.avatarId) ? (
                      <Image
                        src="/favicon.ico"
                        width="45px"
                        height="45px"
<<<<<<< HEAD:client/components/yeonwoo/HallOfFame.tsx
                        alt="avatar"
=======
                        alt="rank"
>>>>>>> ce718f293ca7535492d6168f43ad435fbdfaf9ff:client/components/main/HallOfFame.tsx
                      />
                    ) : (
                      <Image
                        src={
                          rank.avatar ? rank.avatar.remotePath : '/favicon.ico'
                        }
                        width="45px"
                        height="45px"
                        alt="avatar"
                        onError={() =>
                          setIsErrorAvatarId((prev) => [
                            ...prev,
                            rank.avatar?.avatarId,
                          ])
                        }
                        alt="rank"
                      />
                    )}
                  </div>
                </Link>
                <div>
                  <div>
                    <Link href={`/dashboard/${rank.userId}`}>
                      {/* 닉네임에 대한 컴포넌트 */}
                      <span className="text-[15px] font-bold hover:cursor-pointer">
                        {rank.nickname}
                      </span>
                    </Link>
                  </div>
                  <div className="flex justify-between w-[273px]">
                    {/* infoMessage 보여주는 컴포넌트 */}
                    <div>
                      <span className="text-sm">
                        {rank.infoMessage
                          ? `${rank.infoMessage.slice(0, 18)}...`
                          : ''}
                      </span>
                    </div>
                    {/* 랭킹 보여주는 컴포넌트 */}
                    <div>
                      <span className="text-xs mr-2">
                        {changeGradeEmoji(rank.grade ?? '')}
                      </span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          ))}
      </div>
    </>
  );
};
