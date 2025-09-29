class PredictionModel:
    async def process(self, messages):
        raise NotImplementedError("Subclasses should implement this method")