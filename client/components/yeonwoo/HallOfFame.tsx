/*
 * 책임 작성자: 박연우
 * 최초 작성일: 2022-11-18
 * 최근 수정일: 2022-11-29
 */

import axios from 'axios';
import Image from 'next/image';
import { useEffect, useState } from 'react';
import { rankList } from '../../interfaces';

export const HallOfFame = () => {
  const [ranks, setRanks] = useState<rankList[] | null>(null);
  const [errorAvatarId, setIsErrorAvatarId] = useState<(number | undefined)[]>(
    [],
  );
  const getRanks = async () => {
    const res = await axios.get('/api/users/ranks?page=1&size=8');
    setRanks(res.data.data);
  };
  useEffect(() => {
    getRanks();
  }, []);
  return (
    <>
      <div className="mb-10">
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
                <div className="w-[45px] h-[45px] rounded-full overflow-hidden">
                  {errorAvatarId.includes(rank.avatar?.avatarId) ? (
                    <Image src="/favicon.ico" width="45px" height="45px" />
                  ) : (
                    <Image
                      src={
                        rank.avatar ? rank.avatar.remotePath : '/favicon.ico'
                      }
                      width="45px"
                      height="45px"
                      onError={() =>
                        setIsErrorAvatarId((prev) => [
                          ...prev,
                          rank.avatar?.avatarId,
                        ])
                      }
                    />
                  )}
                </div>
                <div>
                  <div>
                    <span className="text-[15px] font-bold">
                      {rank.nickname}
                    </span>
                  </div>
                  <div className="flex justify-between w-[273px]">
                    <div>
                      <span className="text-sm">
                        {rank.infoMessage
                          ? `${rank.infoMessage.slice(0, 18)}...`
                          : ''}
                      </span>
                    </div>
                    <div>
                      <span className="text-xs mr-2">{rank.grade}</span>
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
