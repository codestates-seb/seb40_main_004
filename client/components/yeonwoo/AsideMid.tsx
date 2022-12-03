/*
 * 책임 작성자: 박연우
 * 최초 작성일: 2022-11-19
 * 최근 수정일: 2022-12-03
 */

import { useRecoilValue } from 'recoil';
import { userDashboardAtom } from '../../atomsYW';
import { changeTagPrettier } from '../../libs/changeTagPrettier';

export const AsideMid = () => {
  const userDashboard = useRecoilValue(userDashboardAtom);
  return (
    <div className="mt-[74px]">
      <div className="mb-6">
        <span className="text-2xl font-semibold">기술 태그</span>
      </div>
      <div>
        {userDashboard.tags.map((tag) => (
          <div className="mb-4" key={tag.tag_Id}>
            <button className="bg-main-yellow rounded-full py-[6px] w-32">
              {changeTagPrettier(tag.name)}
            </button>
          </div>
        ))}
      </div>
    </div>
  );
};
