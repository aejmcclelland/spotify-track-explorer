export default function Home() {
	return (
		<div className='font-sans grid grid-rows-[20px_1fr_20px] items-center justify-items-center min-h-screen p-8 pb-20 gap-16 sm:p-20'>
			<main className='flex flex-col gap-[32px] row-start-2 items-center sm:items-start'>
				<p className='font-sans ... text-4xl font-bold'>Spotify Track Explorer</p>
				<p className='font-mono ... text-2xl'>
					The easy way to organise your tracks
				</p>
			</main>
			<footer className='row-start-3 flex gap-[24px] flex-wrap items-center justify-center'></footer>
		</div>
	);
}
