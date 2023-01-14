import { useRecoilValue } from 'recoil';
import { userDashboardAtom } from '../../atoms/userAtom';
import { changeTagPrettier } from '../../libs/changeTagPrettier';

export const AsideMid = () => {
  const userDashboard = useRecoilValue(userDashboardAtom);
  return (
    <div className="mt-[74px]">
      <div className="mb-6">
        <span className="text-xl font-semibold">기술 태그</span>
      </div>
      <div>
        {userDashboard.tags.map((tag) => (
          <div className="mb-4" key={tag.tag_Id}>
            <button
              className="bg-main-yellow rounded-full py-[6px] w-32 text-sm"
              disabled
            >
              {changeTagPrettier(tag.name)}
            </button>
          </div>
        ))}
      </div>
    </div>
  );
};
