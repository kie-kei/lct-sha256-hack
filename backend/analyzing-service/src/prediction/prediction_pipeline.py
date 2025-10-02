import asyncio

class PredictionPipeline:
    def __init__(self, models):
        self.models = models

    async def process_batch(self, itp_id, messages):
        results = await asyncio.gather(
            *[model.process(messages) for model in self.models]
        )
        return {itp_id: results}