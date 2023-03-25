import { confirmAlert } from 'react-confirm-alert';

type getConfirmAlertFactors = {
  title: string;
  message: string;
  submitCallback: () => void;
};

export const getConfirmAlert = ({
  title,
  message,
  submitCallback,
}: getConfirmAlertFactors) => {
  const confrim = confirmAlert({
    title: '',
    message: '',
    buttons: [
      {
        label: '확인',
        onClick: () => alert('Click Yes'),
      },
      {
        label: '취소',
        onClick: () => alert('Click No'),
      },
    ],
  });
  return { confirm };
};
