import { faChevronRight } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { useSetRecoilState } from 'recoil';
import { isLoggedInAtom } from '../../atoms';

export const BtnLogin = () => {
  const setIsLoggedIn = useSetRecoilState(isLoggedInAtom);
  const onClick = () => setIsLoggedIn(true);

  return (
    <button onClick={onClick} className="ml-6 flex gap-2 items-center">
      <span>로그인</span>
      <FontAwesomeIcon icon={faChevronRight} />
    </button>
  );
};
