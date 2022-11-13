import { NextPage } from 'next';
import { Header } from '../../components/common/Header';

const EditProfile: NextPage = () => {
  return (
    <>
      <Header />
      <main className="max-w-[1280px] mx-auto">
        <h1>Edit-Profile</h1>
      </main>
    </>
  );
};

export default EditProfile;
